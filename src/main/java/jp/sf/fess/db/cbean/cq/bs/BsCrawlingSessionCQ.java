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

import jp.sf.fess.db.cbean.CrawlingSessionCB;
import jp.sf.fess.db.cbean.cq.CrawlingSessionCQ;
import jp.sf.fess.db.cbean.cq.CrawlingSessionInfoCQ;
import jp.sf.fess.db.cbean.cq.ciq.CrawlingSessionCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
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
    public BsCrawlingSessionCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from CRAWLING_SESSION) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
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
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
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

    protected Map<String, CrawlingSessionInfoCQ> _id_ExistsReferrer_CrawlingSessionInfoListMap;

    public Map<String, CrawlingSessionInfoCQ> getId_ExistsReferrer_CrawlingSessionInfoList() {
        return _id_ExistsReferrer_CrawlingSessionInfoListMap;
    }

    @Override
    public String keepId_ExistsReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ subQuery) {
        if (_id_ExistsReferrer_CrawlingSessionInfoListMap == null) {
            _id_ExistsReferrer_CrawlingSessionInfoListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_CrawlingSessionInfoListMap.size() + 1);
        _id_ExistsReferrer_CrawlingSessionInfoListMap.put(key, subQuery);
        return "id_ExistsReferrer_CrawlingSessionInfoList." + key;
    }

    protected Map<String, CrawlingSessionInfoCQ> _id_NotExistsReferrer_CrawlingSessionInfoListMap;

    public Map<String, CrawlingSessionInfoCQ> getId_NotExistsReferrer_CrawlingSessionInfoList() {
        return _id_NotExistsReferrer_CrawlingSessionInfoListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ subQuery) {
        if (_id_NotExistsReferrer_CrawlingSessionInfoListMap == null) {
            _id_NotExistsReferrer_CrawlingSessionInfoListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_CrawlingSessionInfoListMap.size() + 1);
        _id_NotExistsReferrer_CrawlingSessionInfoListMap.put(key, subQuery);
        return "id_NotExistsReferrer_CrawlingSessionInfoList." + key;
    }

    protected Map<String, CrawlingSessionInfoCQ> _id_SpecifyDerivedReferrer_CrawlingSessionInfoListMap;

    public Map<String, CrawlingSessionInfoCQ> getId_SpecifyDerivedReferrer_CrawlingSessionInfoList() {
        return _id_SpecifyDerivedReferrer_CrawlingSessionInfoListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_CrawlingSessionInfoListMap == null) {
            _id_SpecifyDerivedReferrer_CrawlingSessionInfoListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_CrawlingSessionInfoListMap.size() + 1);
        _id_SpecifyDerivedReferrer_CrawlingSessionInfoListMap
                .put(key, subQuery);
        return "id_SpecifyDerivedReferrer_CrawlingSessionInfoList." + key;
    }

    protected Map<String, CrawlingSessionInfoCQ> _id_InScopeRelation_CrawlingSessionInfoListMap;

    public Map<String, CrawlingSessionInfoCQ> getId_InScopeRelation_CrawlingSessionInfoList() {
        return _id_InScopeRelation_CrawlingSessionInfoListMap;
    }

    @Override
    public String keepId_InScopeRelation_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ subQuery) {
        if (_id_InScopeRelation_CrawlingSessionInfoListMap == null) {
            _id_InScopeRelation_CrawlingSessionInfoListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_CrawlingSessionInfoListMap.size() + 1);
        _id_InScopeRelation_CrawlingSessionInfoListMap.put(key, subQuery);
        return "id_InScopeRelation_CrawlingSessionInfoList." + key;
    }

    protected Map<String, CrawlingSessionInfoCQ> _id_NotInScopeRelation_CrawlingSessionInfoListMap;

    public Map<String, CrawlingSessionInfoCQ> getId_NotInScopeRelation_CrawlingSessionInfoList() {
        return _id_NotInScopeRelation_CrawlingSessionInfoListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ subQuery) {
        if (_id_NotInScopeRelation_CrawlingSessionInfoListMap == null) {
            _id_NotInScopeRelation_CrawlingSessionInfoListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_CrawlingSessionInfoListMap.size() + 1);
        _id_NotInScopeRelation_CrawlingSessionInfoListMap.put(key, subQuery);
        return "id_NotInScopeRelation_CrawlingSessionInfoList." + key;
    }

    protected Map<String, CrawlingSessionInfoCQ> _id_QueryDerivedReferrer_CrawlingSessionInfoListMap;

    public Map<String, CrawlingSessionInfoCQ> getId_QueryDerivedReferrer_CrawlingSessionInfoList() {
        return _id_QueryDerivedReferrer_CrawlingSessionInfoListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ subQuery) {
        if (_id_QueryDerivedReferrer_CrawlingSessionInfoListMap == null) {
            _id_QueryDerivedReferrer_CrawlingSessionInfoListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_CrawlingSessionInfoListMap.size() + 1);
        _id_QueryDerivedReferrer_CrawlingSessionInfoListMap.put(key, subQuery);
        return "id_QueryDerivedReferrer_CrawlingSessionInfoList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_CrawlingSessionInfoListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_CrawlingSessionInfoListParameter() {
        return _id_QueryDerivedReferrer_CrawlingSessionInfoListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_CrawlingSessionInfoListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_CrawlingSessionInfoListParameterMap == null) {
            _id_QueryDerivedReferrer_CrawlingSessionInfoListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_CrawlingSessionInfoListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_CrawlingSessionInfoListParameterMap.put(key,
                parameterValue);
        return "id_QueryDerivedReferrer_CrawlingSessionInfoListParameter."
                + key;
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
     * NAME: {IX, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {IX, VARCHAR(20)}
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
     * EXPIRED_TIME: {IX+, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_ExpiredTime_Asc() {
        regOBA("EXPIRED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXPIRED_TIME: {IX+, TIMESTAMP(23, 10)}
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #FD4747">aliasName</span>);
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #FD4747">aliasName</span>);
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
    protected Map<String, CrawlingSessionCQ> _scalarConditionMap;

    public Map<String, CrawlingSessionCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final CrawlingSessionCQ subQuery) {
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
    protected Map<String, CrawlingSessionCQ> _specifyMyselfDerivedMap;

    public Map<String, CrawlingSessionCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final CrawlingSessionCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, CrawlingSessionCQ> _queryMyselfDerivedMap;

    public Map<String, CrawlingSessionCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final CrawlingSessionCQ subQuery) {
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
    protected Map<String, CrawlingSessionCQ> _myselfExistsMap;

    public Map<String, CrawlingSessionCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final CrawlingSessionCQ subQuery) {
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
    protected Map<String, CrawlingSessionCQ> _myselfInScopeMap;

    public Map<String, CrawlingSessionCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final CrawlingSessionCQ subQuery) {
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
        return CrawlingSessionCB.class.getName();
    }

    protected String xCQ() {
        return CrawlingSessionCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
