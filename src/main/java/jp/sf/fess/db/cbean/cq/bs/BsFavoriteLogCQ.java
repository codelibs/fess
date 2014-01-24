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
    public BsFavoriteLogCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from FAVORITE_LOG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
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
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
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

    protected Map<String, UserInfoCQ> _userId_InScopeRelation_UserInfoMap;

    public Map<String, UserInfoCQ> getUserId_InScopeRelation_UserInfo() {
        return _userId_InScopeRelation_UserInfoMap;
    }

    @Override
    public String keepUserId_InScopeRelation_UserInfo(final UserInfoCQ subQuery) {
        if (_userId_InScopeRelation_UserInfoMap == null) {
            _userId_InScopeRelation_UserInfoMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_userId_InScopeRelation_UserInfoMap.size() + 1);
        _userId_InScopeRelation_UserInfoMap.put(key, subQuery);
        return "userId_InScopeRelation_UserInfo." + key;
    }

    protected Map<String, UserInfoCQ> _userId_NotInScopeRelation_UserInfoMap;

    public Map<String, UserInfoCQ> getUserId_NotInScopeRelation_UserInfo() {
        return _userId_NotInScopeRelation_UserInfoMap;
    }

    @Override
    public String keepUserId_NotInScopeRelation_UserInfo(
            final UserInfoCQ subQuery) {
        if (_userId_NotInScopeRelation_UserInfoMap == null) {
            _userId_NotInScopeRelation_UserInfoMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_userId_NotInScopeRelation_UserInfoMap.size() + 1);
        _userId_NotInScopeRelation_UserInfoMap.put(key, subQuery);
        return "userId_NotInScopeRelation_UserInfo." + key;
    }

    /** 
     * Add order-by as ascend. <br />
     * USER_ID: {UQ, IX, NotNull, BIGINT(19), FK to USER_INFO}
     * @return this. (NotNull)
     */
    public BsFavoriteLogCQ addOrderBy_UserId_Asc() {
        regOBA("USER_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * USER_ID: {UQ, IX, NotNull, BIGINT(19), FK to USER_INFO}
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
     * URL: {UQ+, NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFavoriteLogCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * URL: {UQ+, NotNull, VARCHAR(4000)}
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #FD4747">aliasName</span>);
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #FD4747">aliasName</span>);
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
    protected void reflectRelationOnUnionQuery(
            final ConditionQuery baseQueryAsSuper,
            final ConditionQuery unionQueryAsSuper) {
        final FavoriteLogCQ baseQuery = (FavoriteLogCQ) baseQueryAsSuper;
        final FavoriteLogCQ unionQuery = (FavoriteLogCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryUserInfo()) {
            unionQuery.queryUserInfo().reflectRelationOnUnionQuery(
                    baseQuery.queryUserInfo(), unionQuery.queryUserInfo());
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

    protected UserInfoCQ _conditionQueryUserInfo;

    public UserInfoCQ getConditionQueryUserInfo() {
        if (_conditionQueryUserInfo == null) {
            _conditionQueryUserInfo = xcreateQueryUserInfo();
            xsetupOuterJoinUserInfo();
        }
        return _conditionQueryUserInfo;
    }

    protected UserInfoCQ xcreateQueryUserInfo() {
        final String nrp = resolveNextRelationPath("FAVORITE_LOG", "userInfo");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final UserInfoCQ cq = new UserInfoCQ(this, xgetSqlClause(), jan,
                xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("userInfo");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinUserInfo() {
        final UserInfoCQ cq = getConditionQueryUserInfo();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("USER_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "userInfo");
    }

    public boolean hasConditionQueryUserInfo() {
        return _conditionQueryUserInfo != null;
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, FavoriteLogCQ> _scalarConditionMap;

    public Map<String, FavoriteLogCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final FavoriteLogCQ subQuery) {
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
    protected Map<String, FavoriteLogCQ> _specifyMyselfDerivedMap;

    public Map<String, FavoriteLogCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final FavoriteLogCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, FavoriteLogCQ> _queryMyselfDerivedMap;

    public Map<String, FavoriteLogCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final FavoriteLogCQ subQuery) {
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
    protected Map<String, FavoriteLogCQ> _myselfExistsMap;

    public Map<String, FavoriteLogCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final FavoriteLogCQ subQuery) {
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
    protected Map<String, FavoriteLogCQ> _myselfInScopeMap;

    public Map<String, FavoriteLogCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final FavoriteLogCQ subQuery) {
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
        return FavoriteLogCB.class.getName();
    }

    protected String xCQ() {
        return FavoriteLogCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
