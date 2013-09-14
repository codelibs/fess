/*
 * Copyright 2009-2013 the Fess Project and the Others.
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
    public BsUserInfoCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from USER_INFO) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
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
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
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

    protected Map<String, FavoriteLogCQ> _id_ExistsReferrer_FavoriteLogListMap;

    public Map<String, FavoriteLogCQ> getId_ExistsReferrer_FavoriteLogList() {
        return _id_ExistsReferrer_FavoriteLogListMap;
    }

    @Override
    public String keepId_ExistsReferrer_FavoriteLogList(
            final FavoriteLogCQ subQuery) {
        if (_id_ExistsReferrer_FavoriteLogListMap == null) {
            _id_ExistsReferrer_FavoriteLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_FavoriteLogListMap.size() + 1);
        _id_ExistsReferrer_FavoriteLogListMap.put(key, subQuery);
        return "id_ExistsReferrer_FavoriteLogList." + key;
    }

    protected Map<String, SearchLogCQ> _id_ExistsReferrer_SearchLogListMap;

    public Map<String, SearchLogCQ> getId_ExistsReferrer_SearchLogList() {
        return _id_ExistsReferrer_SearchLogListMap;
    }

    @Override
    public String keepId_ExistsReferrer_SearchLogList(final SearchLogCQ subQuery) {
        if (_id_ExistsReferrer_SearchLogListMap == null) {
            _id_ExistsReferrer_SearchLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_SearchLogListMap.size() + 1);
        _id_ExistsReferrer_SearchLogListMap.put(key, subQuery);
        return "id_ExistsReferrer_SearchLogList." + key;
    }

    protected Map<String, FavoriteLogCQ> _id_NotExistsReferrer_FavoriteLogListMap;

    public Map<String, FavoriteLogCQ> getId_NotExistsReferrer_FavoriteLogList() {
        return _id_NotExistsReferrer_FavoriteLogListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_FavoriteLogList(
            final FavoriteLogCQ subQuery) {
        if (_id_NotExistsReferrer_FavoriteLogListMap == null) {
            _id_NotExistsReferrer_FavoriteLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_FavoriteLogListMap.size() + 1);
        _id_NotExistsReferrer_FavoriteLogListMap.put(key, subQuery);
        return "id_NotExistsReferrer_FavoriteLogList." + key;
    }

    protected Map<String, SearchLogCQ> _id_NotExistsReferrer_SearchLogListMap;

    public Map<String, SearchLogCQ> getId_NotExistsReferrer_SearchLogList() {
        return _id_NotExistsReferrer_SearchLogListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_SearchLogList(
            final SearchLogCQ subQuery) {
        if (_id_NotExistsReferrer_SearchLogListMap == null) {
            _id_NotExistsReferrer_SearchLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_SearchLogListMap.size() + 1);
        _id_NotExistsReferrer_SearchLogListMap.put(key, subQuery);
        return "id_NotExistsReferrer_SearchLogList." + key;
    }

    protected Map<String, FavoriteLogCQ> _id_SpecifyDerivedReferrer_FavoriteLogListMap;

    public Map<String, FavoriteLogCQ> getId_SpecifyDerivedReferrer_FavoriteLogList() {
        return _id_SpecifyDerivedReferrer_FavoriteLogListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FavoriteLogList(
            final FavoriteLogCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_FavoriteLogListMap == null) {
            _id_SpecifyDerivedReferrer_FavoriteLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_FavoriteLogListMap.size() + 1);
        _id_SpecifyDerivedReferrer_FavoriteLogListMap.put(key, subQuery);
        return "id_SpecifyDerivedReferrer_FavoriteLogList." + key;
    }

    protected Map<String, SearchLogCQ> _id_SpecifyDerivedReferrer_SearchLogListMap;

    public Map<String, SearchLogCQ> getId_SpecifyDerivedReferrer_SearchLogList() {
        return _id_SpecifyDerivedReferrer_SearchLogListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_SearchLogList(
            final SearchLogCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_SearchLogListMap == null) {
            _id_SpecifyDerivedReferrer_SearchLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_SearchLogListMap.size() + 1);
        _id_SpecifyDerivedReferrer_SearchLogListMap.put(key, subQuery);
        return "id_SpecifyDerivedReferrer_SearchLogList." + key;
    }

    protected Map<String, FavoriteLogCQ> _id_InScopeRelation_FavoriteLogListMap;

    public Map<String, FavoriteLogCQ> getId_InScopeRelation_FavoriteLogList() {
        return _id_InScopeRelation_FavoriteLogListMap;
    }

    @Override
    public String keepId_InScopeRelation_FavoriteLogList(
            final FavoriteLogCQ subQuery) {
        if (_id_InScopeRelation_FavoriteLogListMap == null) {
            _id_InScopeRelation_FavoriteLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_FavoriteLogListMap.size() + 1);
        _id_InScopeRelation_FavoriteLogListMap.put(key, subQuery);
        return "id_InScopeRelation_FavoriteLogList." + key;
    }

    protected Map<String, SearchLogCQ> _id_InScopeRelation_SearchLogListMap;

    public Map<String, SearchLogCQ> getId_InScopeRelation_SearchLogList() {
        return _id_InScopeRelation_SearchLogListMap;
    }

    @Override
    public String keepId_InScopeRelation_SearchLogList(
            final SearchLogCQ subQuery) {
        if (_id_InScopeRelation_SearchLogListMap == null) {
            _id_InScopeRelation_SearchLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_SearchLogListMap.size() + 1);
        _id_InScopeRelation_SearchLogListMap.put(key, subQuery);
        return "id_InScopeRelation_SearchLogList." + key;
    }

    protected Map<String, FavoriteLogCQ> _id_NotInScopeRelation_FavoriteLogListMap;

    public Map<String, FavoriteLogCQ> getId_NotInScopeRelation_FavoriteLogList() {
        return _id_NotInScopeRelation_FavoriteLogListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_FavoriteLogList(
            final FavoriteLogCQ subQuery) {
        if (_id_NotInScopeRelation_FavoriteLogListMap == null) {
            _id_NotInScopeRelation_FavoriteLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_FavoriteLogListMap.size() + 1);
        _id_NotInScopeRelation_FavoriteLogListMap.put(key, subQuery);
        return "id_NotInScopeRelation_FavoriteLogList." + key;
    }

    protected Map<String, SearchLogCQ> _id_NotInScopeRelation_SearchLogListMap;

    public Map<String, SearchLogCQ> getId_NotInScopeRelation_SearchLogList() {
        return _id_NotInScopeRelation_SearchLogListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_SearchLogList(
            final SearchLogCQ subQuery) {
        if (_id_NotInScopeRelation_SearchLogListMap == null) {
            _id_NotInScopeRelation_SearchLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_SearchLogListMap.size() + 1);
        _id_NotInScopeRelation_SearchLogListMap.put(key, subQuery);
        return "id_NotInScopeRelation_SearchLogList." + key;
    }

    protected Map<String, FavoriteLogCQ> _id_QueryDerivedReferrer_FavoriteLogListMap;

    public Map<String, FavoriteLogCQ> getId_QueryDerivedReferrer_FavoriteLogList() {
        return _id_QueryDerivedReferrer_FavoriteLogListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FavoriteLogList(
            final FavoriteLogCQ subQuery) {
        if (_id_QueryDerivedReferrer_FavoriteLogListMap == null) {
            _id_QueryDerivedReferrer_FavoriteLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_FavoriteLogListMap.size() + 1);
        _id_QueryDerivedReferrer_FavoriteLogListMap.put(key, subQuery);
        return "id_QueryDerivedReferrer_FavoriteLogList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_FavoriteLogListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_FavoriteLogListParameter() {
        return _id_QueryDerivedReferrer_FavoriteLogListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FavoriteLogListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_FavoriteLogListParameterMap == null) {
            _id_QueryDerivedReferrer_FavoriteLogListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_FavoriteLogListParameterMap.size() + 1);
        _id_QueryDerivedReferrer_FavoriteLogListParameterMap.put(key,
                parameterValue);
        return "id_QueryDerivedReferrer_FavoriteLogListParameter." + key;
    }

    protected Map<String, SearchLogCQ> _id_QueryDerivedReferrer_SearchLogListMap;

    public Map<String, SearchLogCQ> getId_QueryDerivedReferrer_SearchLogList() {
        return _id_QueryDerivedReferrer_SearchLogListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchLogList(
            final SearchLogCQ subQuery) {
        if (_id_QueryDerivedReferrer_SearchLogListMap == null) {
            _id_QueryDerivedReferrer_SearchLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_SearchLogListMap.size() + 1);
        _id_QueryDerivedReferrer_SearchLogListMap.put(key, subQuery);
        return "id_QueryDerivedReferrer_SearchLogList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_SearchLogListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_SearchLogListParameter() {
        return _id_QueryDerivedReferrer_SearchLogListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchLogListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_SearchLogListParameterMap == null) {
            _id_QueryDerivedReferrer_SearchLogListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_SearchLogListParameterMap.size() + 1);
        _id_QueryDerivedReferrer_SearchLogListParameterMap.put(key,
                parameterValue);
        return "id_QueryDerivedReferrer_SearchLogListParameter." + key;
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #FD4747">aliasName</span>);
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #FD4747">aliasName</span>);
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
    protected void reflectRelationOnUnionQuery(
            final ConditionQuery baseQueryAsSuper,
            final ConditionQuery unionQueryAsSuper) {
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
    protected Map<String, UserInfoCQ> _scalarConditionMap;

    public Map<String, UserInfoCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final UserInfoCQ subQuery) {
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
    protected Map<String, UserInfoCQ> _specifyMyselfDerivedMap;

    public Map<String, UserInfoCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final UserInfoCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, UserInfoCQ> _queryMyselfDerivedMap;

    public Map<String, UserInfoCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final UserInfoCQ subQuery) {
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
    protected Map<String, UserInfoCQ> _myselfExistsMap;

    public Map<String, UserInfoCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final UserInfoCQ subQuery) {
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
    protected Map<String, UserInfoCQ> _myselfInScopeMap;

    public Map<String, UserInfoCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final UserInfoCQ subQuery) {
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
        return UserInfoCB.class.getName();
    }

    protected String xCQ() {
        return UserInfoCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
