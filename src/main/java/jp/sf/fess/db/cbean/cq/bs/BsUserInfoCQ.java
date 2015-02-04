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

import jp.sf.fess.db.cbean.UserInfoCB;
import jp.sf.fess.db.cbean.cq.FavoriteLogCQ;
import jp.sf.fess.db.cbean.cq.SearchLogCQ;
import jp.sf.fess.db.cbean.cq.UserInfoCQ;
import jp.sf.fess.db.cbean.cq.ciq.UserInfoCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of USER_INFO.
 * @author DBFlute(AutoGenerator)
 */
public class BsUserInfoCQ extends AbstractBsUserInfoCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected UserInfoCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsUserInfoCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from USER_INFO) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public UserInfoCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected UserInfoCIQ xcreateCIQ() {
        final UserInfoCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected UserInfoCIQ xnewCIQ() {
        return new UserInfoCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join USER_INFO on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public UserInfoCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final UserInfoCIQ inlineQuery = inline();
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

    public Map<String, FavoriteLogCQ> getId_ExistsReferrer_FavoriteLogList() {
        return xgetSQueMap("id_ExistsReferrer_FavoriteLogList");
    }

    @Override
    public String keepId_ExistsReferrer_FavoriteLogList(final FavoriteLogCQ sq) {
        return xkeepSQue("id_ExistsReferrer_FavoriteLogList", sq);
    }

    public Map<String, SearchLogCQ> getId_ExistsReferrer_SearchLogList() {
        return xgetSQueMap("id_ExistsReferrer_SearchLogList");
    }

    @Override
    public String keepId_ExistsReferrer_SearchLogList(final SearchLogCQ sq) {
        return xkeepSQue("id_ExistsReferrer_SearchLogList", sq);
    }

    public Map<String, FavoriteLogCQ> getId_NotExistsReferrer_FavoriteLogList() {
        return xgetSQueMap("id_NotExistsReferrer_FavoriteLogList");
    }

    @Override
    public String keepId_NotExistsReferrer_FavoriteLogList(
            final FavoriteLogCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_FavoriteLogList", sq);
    }

    public Map<String, SearchLogCQ> getId_NotExistsReferrer_SearchLogList() {
        return xgetSQueMap("id_NotExistsReferrer_SearchLogList");
    }

    @Override
    public String keepId_NotExistsReferrer_SearchLogList(final SearchLogCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_SearchLogList", sq);
    }

    public Map<String, FavoriteLogCQ> getId_SpecifyDerivedReferrer_FavoriteLogList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_FavoriteLogList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FavoriteLogList(
            final FavoriteLogCQ sq) {
        return xkeepSQue("id_SpecifyDerivedReferrer_FavoriteLogList", sq);
    }

    public Map<String, SearchLogCQ> getId_SpecifyDerivedReferrer_SearchLogList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_SearchLogList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_SearchLogList(
            final SearchLogCQ sq) {
        return xkeepSQue("id_SpecifyDerivedReferrer_SearchLogList", sq);
    }

    public Map<String, FavoriteLogCQ> getId_InScopeRelation_FavoriteLogList() {
        return xgetSQueMap("id_InScopeRelation_FavoriteLogList");
    }

    @Override
    public String keepId_InScopeRelation_FavoriteLogList(final FavoriteLogCQ sq) {
        return xkeepSQue("id_InScopeRelation_FavoriteLogList", sq);
    }

    public Map<String, SearchLogCQ> getId_InScopeRelation_SearchLogList() {
        return xgetSQueMap("id_InScopeRelation_SearchLogList");
    }

    @Override
    public String keepId_InScopeRelation_SearchLogList(final SearchLogCQ sq) {
        return xkeepSQue("id_InScopeRelation_SearchLogList", sq);
    }

    public Map<String, FavoriteLogCQ> getId_NotInScopeRelation_FavoriteLogList() {
        return xgetSQueMap("id_NotInScopeRelation_FavoriteLogList");
    }

    @Override
    public String keepId_NotInScopeRelation_FavoriteLogList(
            final FavoriteLogCQ sq) {
        return xkeepSQue("id_NotInScopeRelation_FavoriteLogList", sq);
    }

    public Map<String, SearchLogCQ> getId_NotInScopeRelation_SearchLogList() {
        return xgetSQueMap("id_NotInScopeRelation_SearchLogList");
    }

    @Override
    public String keepId_NotInScopeRelation_SearchLogList(final SearchLogCQ sq) {
        return xkeepSQue("id_NotInScopeRelation_SearchLogList", sq);
    }

    public Map<String, FavoriteLogCQ> getId_QueryDerivedReferrer_FavoriteLogList() {
        return xgetSQueMap("id_QueryDerivedReferrer_FavoriteLogList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FavoriteLogList(
            final FavoriteLogCQ sq) {
        return xkeepSQue("id_QueryDerivedReferrer_FavoriteLogList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_FavoriteLogListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_FavoriteLogList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FavoriteLogListParameter(
            final Object pm) {
        return xkeepSQuePm("id_QueryDerivedReferrer_FavoriteLogList", pm);
    }

    public Map<String, SearchLogCQ> getId_QueryDerivedReferrer_SearchLogList() {
        return xgetSQueMap("id_QueryDerivedReferrer_SearchLogList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchLogList(final SearchLogCQ sq) {
        return xkeepSQue("id_QueryDerivedReferrer_SearchLogList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_SearchLogListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_SearchLogList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchLogListParameter(
            final Object pm) {
        return xkeepSQuePm("id_QueryDerivedReferrer_SearchLogList", pm);
    }

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUserInfoCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsUserInfoCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _code;

    public ConditionValue getCode() {
        if (_code == null) {
            _code = nCV();
        }
        return _code;
    }

    @Override
    protected ConditionValue getCValueCode() {
        return getCode();
    }

    /**
     * Add order-by as ascend. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @return this. (NotNull)
     */
    public BsUserInfoCQ addOrderBy_Code_Asc() {
        regOBA("CODE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CODE: {NotNull, VARCHAR(1000)}
     * @return this. (NotNull)
     */
    public BsUserInfoCQ addOrderBy_Code_Desc() {
        regOBD("CODE");
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
    public BsUserInfoCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsUserInfoCQ addOrderBy_CreatedTime_Desc() {
        regOBD("CREATED_TIME");
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
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsUserInfoCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsUserInfoCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("UPDATED_TIME");
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
    public BsUserInfoCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsUserInfoCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
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
    public Map<String, UserInfoCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final UserInfoCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, UserInfoCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final UserInfoCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, UserInfoCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final UserInfoCQ sq) {
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
    protected Map<String, UserInfoCQ> _myselfExistsMap;

    public Map<String, UserInfoCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final UserInfoCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, UserInfoCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final UserInfoCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return UserInfoCB.class.getName();
    }

    protected String xCQ() {
        return UserInfoCQ.class.getName();
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
