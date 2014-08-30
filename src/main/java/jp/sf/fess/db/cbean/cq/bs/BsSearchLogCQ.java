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

import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.cbean.cq.ClickLogCQ;
import jp.sf.fess.db.cbean.cq.SearchFieldLogCQ;
import jp.sf.fess.db.cbean.cq.SearchLogCQ;
import jp.sf.fess.db.cbean.cq.UserInfoCQ;
import jp.sf.fess.db.cbean.cq.ciq.SearchLogCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of SEARCH_LOG.
 * @author DBFlute(AutoGenerator)
 */
public class BsSearchLogCQ extends AbstractBsSearchLogCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected SearchLogCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsSearchLogCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from SEARCH_LOG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public SearchLogCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected SearchLogCIQ xcreateCIQ() {
        final SearchLogCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected SearchLogCIQ xnewCIQ() {
        return new SearchLogCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join SEARCH_LOG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public SearchLogCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final SearchLogCIQ inlineQuery = inline();
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

    public Map<String, ClickLogCQ> getId_ExistsReferrer_ClickLogList() {
        return xgetSQueMap("id_ExistsReferrer_ClickLogList");
    }

    @Override
    public String keepId_ExistsReferrer_ClickLogList(final ClickLogCQ sq) {
        return xkeepSQue("id_ExistsReferrer_ClickLogList", sq);
    }

    public Map<String, SearchFieldLogCQ> getId_ExistsReferrer_SearchFieldLogList() {
        return xgetSQueMap("id_ExistsReferrer_SearchFieldLogList");
    }

    @Override
    public String keepId_ExistsReferrer_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        return xkeepSQue("id_ExistsReferrer_SearchFieldLogList", sq);
    }

    public Map<String, ClickLogCQ> getId_NotExistsReferrer_ClickLogList() {
        return xgetSQueMap("id_NotExistsReferrer_ClickLogList");
    }

    @Override
    public String keepId_NotExistsReferrer_ClickLogList(final ClickLogCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_ClickLogList", sq);
    }

    public Map<String, SearchFieldLogCQ> getId_NotExistsReferrer_SearchFieldLogList() {
        return xgetSQueMap("id_NotExistsReferrer_SearchFieldLogList");
    }

    @Override
    public String keepId_NotExistsReferrer_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_SearchFieldLogList", sq);
    }

    public Map<String, ClickLogCQ> getId_SpecifyDerivedReferrer_ClickLogList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_ClickLogList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_ClickLogList(final ClickLogCQ sq) {
        return xkeepSQue("id_SpecifyDerivedReferrer_ClickLogList", sq);
    }

    public Map<String, SearchFieldLogCQ> getId_SpecifyDerivedReferrer_SearchFieldLogList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_SearchFieldLogList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        return xkeepSQue("id_SpecifyDerivedReferrer_SearchFieldLogList", sq);
    }

    public Map<String, ClickLogCQ> getId_InScopeRelation_ClickLogList() {
        return xgetSQueMap("id_InScopeRelation_ClickLogList");
    }

    @Override
    public String keepId_InScopeRelation_ClickLogList(final ClickLogCQ sq) {
        return xkeepSQue("id_InScopeRelation_ClickLogList", sq);
    }

    public Map<String, SearchFieldLogCQ> getId_InScopeRelation_SearchFieldLogList() {
        return xgetSQueMap("id_InScopeRelation_SearchFieldLogList");
    }

    @Override
    public String keepId_InScopeRelation_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        return xkeepSQue("id_InScopeRelation_SearchFieldLogList", sq);
    }

    public Map<String, ClickLogCQ> getId_NotInScopeRelation_ClickLogList() {
        return xgetSQueMap("id_NotInScopeRelation_ClickLogList");
    }

    @Override
    public String keepId_NotInScopeRelation_ClickLogList(final ClickLogCQ sq) {
        return xkeepSQue("id_NotInScopeRelation_ClickLogList", sq);
    }

    public Map<String, SearchFieldLogCQ> getId_NotInScopeRelation_SearchFieldLogList() {
        return xgetSQueMap("id_NotInScopeRelation_SearchFieldLogList");
    }

    @Override
    public String keepId_NotInScopeRelation_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        return xkeepSQue("id_NotInScopeRelation_SearchFieldLogList", sq);
    }

    public Map<String, ClickLogCQ> getId_QueryDerivedReferrer_ClickLogList() {
        return xgetSQueMap("id_QueryDerivedReferrer_ClickLogList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_ClickLogList(final ClickLogCQ sq) {
        return xkeepSQue("id_QueryDerivedReferrer_ClickLogList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_ClickLogListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_ClickLogList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_ClickLogListParameter(
            final Object pm) {
        return xkeepSQuePm("id_QueryDerivedReferrer_ClickLogList", pm);
    }

    public Map<String, SearchFieldLogCQ> getId_QueryDerivedReferrer_SearchFieldLogList() {
        return xgetSQueMap("id_QueryDerivedReferrer_SearchFieldLogList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        return xkeepSQue("id_QueryDerivedReferrer_SearchFieldLogList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_SearchFieldLogListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_SearchFieldLogList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchFieldLogListParameter(
            final Object pm) {
        return xkeepSQuePm("id_QueryDerivedReferrer_SearchFieldLogList", pm);
    }

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _searchWord;

    public ConditionValue getSearchWord() {
        if (_searchWord == null) {
            _searchWord = nCV();
        }
        return _searchWord;
    }

    @Override
    protected ConditionValue getCValueSearchWord() {
        return getSearchWord();
    }

    /**
     * Add order-by as ascend. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_SearchWord_Asc() {
        regOBA("SEARCH_WORD");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_SearchWord_Desc() {
        regOBD("SEARCH_WORD");
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
     * REQUESTED_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_RequestedTime_Asc() {
        regOBA("REQUESTED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * REQUESTED_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_RequestedTime_Desc() {
        regOBD("REQUESTED_TIME");
        return this;
    }

    protected ConditionValue _responseTime;

    public ConditionValue getResponseTime() {
        if (_responseTime == null) {
            _responseTime = nCV();
        }
        return _responseTime;
    }

    @Override
    protected ConditionValue getCValueResponseTime() {
        return getResponseTime();
    }

    /**
     * Add order-by as ascend. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_ResponseTime_Asc() {
        regOBA("RESPONSE_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_ResponseTime_Desc() {
        regOBD("RESPONSE_TIME");
        return this;
    }

    protected ConditionValue _hitCount;

    public ConditionValue getHitCount() {
        if (_hitCount == null) {
            _hitCount = nCV();
        }
        return _hitCount;
    }

    @Override
    protected ConditionValue getCValueHitCount() {
        return getHitCount();
    }

    /**
     * Add order-by as ascend. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_HitCount_Asc() {
        regOBA("HIT_COUNT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_HitCount_Desc() {
        regOBD("HIT_COUNT");
        return this;
    }

    protected ConditionValue _queryOffset;

    public ConditionValue getQueryOffset() {
        if (_queryOffset == null) {
            _queryOffset = nCV();
        }
        return _queryOffset;
    }

    @Override
    protected ConditionValue getCValueQueryOffset() {
        return getQueryOffset();
    }

    /**
     * Add order-by as ascend. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_QueryOffset_Asc() {
        regOBA("QUERY_OFFSET");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_QueryOffset_Desc() {
        regOBD("QUERY_OFFSET");
        return this;
    }

    protected ConditionValue _queryPageSize;

    public ConditionValue getQueryPageSize() {
        if (_queryPageSize == null) {
            _queryPageSize = nCV();
        }
        return _queryPageSize;
    }

    @Override
    protected ConditionValue getCValueQueryPageSize() {
        return getQueryPageSize();
    }

    /**
     * Add order-by as ascend. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_QueryPageSize_Asc() {
        regOBA("QUERY_PAGE_SIZE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_QueryPageSize_Desc() {
        regOBD("QUERY_PAGE_SIZE");
        return this;
    }

    protected ConditionValue _userAgent;

    public ConditionValue getUserAgent() {
        if (_userAgent == null) {
            _userAgent = nCV();
        }
        return _userAgent;
    }

    @Override
    protected ConditionValue getCValueUserAgent() {
        return getUserAgent();
    }

    /**
     * Add order-by as ascend. <br />
     * USER_AGENT: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_UserAgent_Asc() {
        regOBA("USER_AGENT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * USER_AGENT: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_UserAgent_Desc() {
        regOBD("USER_AGENT");
        return this;
    }

    protected ConditionValue _referer;

    public ConditionValue getReferer() {
        if (_referer == null) {
            _referer = nCV();
        }
        return _referer;
    }

    @Override
    protected ConditionValue getCValueReferer() {
        return getReferer();
    }

    /**
     * Add order-by as ascend. <br />
     * REFERER: {VARCHAR(1000)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_Referer_Asc() {
        regOBA("REFERER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * REFERER: {VARCHAR(1000)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_Referer_Desc() {
        regOBD("REFERER");
        return this;
    }

    protected ConditionValue _clientIp;

    public ConditionValue getClientIp() {
        if (_clientIp == null) {
            _clientIp = nCV();
        }
        return _clientIp;
    }

    @Override
    protected ConditionValue getCValueClientIp() {
        return getClientIp();
    }

    /**
     * Add order-by as ascend. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_ClientIp_Asc() {
        regOBA("CLIENT_IP");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CLIENT_IP: {VARCHAR(50)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_ClientIp_Desc() {
        regOBD("CLIENT_IP");
        return this;
    }

    protected ConditionValue _userSessionId;

    public ConditionValue getUserSessionId() {
        if (_userSessionId == null) {
            _userSessionId = nCV();
        }
        return _userSessionId;
    }

    @Override
    protected ConditionValue getCValueUserSessionId() {
        return getUserSessionId();
    }

    /**
     * Add order-by as ascend. <br />
     * USER_SESSION_ID: {VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_UserSessionId_Asc() {
        regOBA("USER_SESSION_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * USER_SESSION_ID: {VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_UserSessionId_Desc() {
        regOBD("USER_SESSION_ID");
        return this;
    }

    protected ConditionValue _accessType;

    public ConditionValue getAccessType() {
        if (_accessType == null) {
            _accessType = nCV();
        }
        return _accessType;
    }

    @Override
    protected ConditionValue getCValueAccessType() {
        return getAccessType();
    }

    /**
     * Add order-by as ascend. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_AccessType_Asc() {
        regOBA("ACCESS_TYPE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_AccessType_Desc() {
        regOBD("ACCESS_TYPE");
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
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_UserId_Asc() {
        regOBA("USER_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_UserId_Desc() {
        regOBD("USER_ID");
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
    public BsSearchLogCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsSearchLogCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    @Override
    public void reflectRelationOnUnionQuery(final ConditionQuery bqs,
            final ConditionQuery uqs) {
        final SearchLogCQ bq = (SearchLogCQ) bqs;
        final SearchLogCQ uq = (SearchLogCQ) uqs;
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
        final String nrp = xresolveNRP("SEARCH_LOG", "userInfo");
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
    public Map<String, SearchLogCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final SearchLogCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, SearchLogCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final SearchLogCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, SearchLogCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final SearchLogCQ sq) {
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
    protected Map<String, SearchLogCQ> _myselfExistsMap;

    public Map<String, SearchLogCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final SearchLogCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, SearchLogCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final SearchLogCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return SearchLogCB.class.getName();
    }

    protected String xCQ() {
        return SearchLogCQ.class.getName();
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
