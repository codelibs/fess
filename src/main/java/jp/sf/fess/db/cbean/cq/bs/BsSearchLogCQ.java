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
    public BsSearchLogCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from SEARCH_LOG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
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
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
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

    protected Map<String, ClickLogCQ> _id_ExistsReferrer_ClickLogListMap;

    public Map<String, ClickLogCQ> getId_ExistsReferrer_ClickLogList() {
        return _id_ExistsReferrer_ClickLogListMap;
    }

    @Override
    public String keepId_ExistsReferrer_ClickLogList(final ClickLogCQ subQuery) {
        if (_id_ExistsReferrer_ClickLogListMap == null) {
            _id_ExistsReferrer_ClickLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_ClickLogListMap.size() + 1);
        _id_ExistsReferrer_ClickLogListMap.put(key, subQuery);
        return "id_ExistsReferrer_ClickLogList." + key;
    }

    protected Map<String, SearchFieldLogCQ> _id_ExistsReferrer_SearchFieldLogListMap;

    public Map<String, SearchFieldLogCQ> getId_ExistsReferrer_SearchFieldLogList() {
        return _id_ExistsReferrer_SearchFieldLogListMap;
    }

    @Override
    public String keepId_ExistsReferrer_SearchFieldLogList(
            final SearchFieldLogCQ subQuery) {
        if (_id_ExistsReferrer_SearchFieldLogListMap == null) {
            _id_ExistsReferrer_SearchFieldLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_SearchFieldLogListMap.size() + 1);
        _id_ExistsReferrer_SearchFieldLogListMap.put(key, subQuery);
        return "id_ExistsReferrer_SearchFieldLogList." + key;
    }

    protected Map<String, ClickLogCQ> _id_NotExistsReferrer_ClickLogListMap;

    public Map<String, ClickLogCQ> getId_NotExistsReferrer_ClickLogList() {
        return _id_NotExistsReferrer_ClickLogListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_ClickLogList(
            final ClickLogCQ subQuery) {
        if (_id_NotExistsReferrer_ClickLogListMap == null) {
            _id_NotExistsReferrer_ClickLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_ClickLogListMap.size() + 1);
        _id_NotExistsReferrer_ClickLogListMap.put(key, subQuery);
        return "id_NotExistsReferrer_ClickLogList." + key;
    }

    protected Map<String, SearchFieldLogCQ> _id_NotExistsReferrer_SearchFieldLogListMap;

    public Map<String, SearchFieldLogCQ> getId_NotExistsReferrer_SearchFieldLogList() {
        return _id_NotExistsReferrer_SearchFieldLogListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_SearchFieldLogList(
            final SearchFieldLogCQ subQuery) {
        if (_id_NotExistsReferrer_SearchFieldLogListMap == null) {
            _id_NotExistsReferrer_SearchFieldLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_SearchFieldLogListMap.size() + 1);
        _id_NotExistsReferrer_SearchFieldLogListMap.put(key, subQuery);
        return "id_NotExistsReferrer_SearchFieldLogList." + key;
    }

    protected Map<String, ClickLogCQ> _id_SpecifyDerivedReferrer_ClickLogListMap;

    public Map<String, ClickLogCQ> getId_SpecifyDerivedReferrer_ClickLogList() {
        return _id_SpecifyDerivedReferrer_ClickLogListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_ClickLogList(
            final ClickLogCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_ClickLogListMap == null) {
            _id_SpecifyDerivedReferrer_ClickLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_ClickLogListMap.size() + 1);
        _id_SpecifyDerivedReferrer_ClickLogListMap.put(key, subQuery);
        return "id_SpecifyDerivedReferrer_ClickLogList." + key;
    }

    protected Map<String, SearchFieldLogCQ> _id_SpecifyDerivedReferrer_SearchFieldLogListMap;

    public Map<String, SearchFieldLogCQ> getId_SpecifyDerivedReferrer_SearchFieldLogList() {
        return _id_SpecifyDerivedReferrer_SearchFieldLogListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_SearchFieldLogList(
            final SearchFieldLogCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_SearchFieldLogListMap == null) {
            _id_SpecifyDerivedReferrer_SearchFieldLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_SearchFieldLogListMap.size() + 1);
        _id_SpecifyDerivedReferrer_SearchFieldLogListMap.put(key, subQuery);
        return "id_SpecifyDerivedReferrer_SearchFieldLogList." + key;
    }

    protected Map<String, ClickLogCQ> _id_InScopeRelation_ClickLogListMap;

    public Map<String, ClickLogCQ> getId_InScopeRelation_ClickLogList() {
        return _id_InScopeRelation_ClickLogListMap;
    }

    @Override
    public String keepId_InScopeRelation_ClickLogList(final ClickLogCQ subQuery) {
        if (_id_InScopeRelation_ClickLogListMap == null) {
            _id_InScopeRelation_ClickLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_ClickLogListMap.size() + 1);
        _id_InScopeRelation_ClickLogListMap.put(key, subQuery);
        return "id_InScopeRelation_ClickLogList." + key;
    }

    protected Map<String, SearchFieldLogCQ> _id_InScopeRelation_SearchFieldLogListMap;

    public Map<String, SearchFieldLogCQ> getId_InScopeRelation_SearchFieldLogList() {
        return _id_InScopeRelation_SearchFieldLogListMap;
    }

    @Override
    public String keepId_InScopeRelation_SearchFieldLogList(
            final SearchFieldLogCQ subQuery) {
        if (_id_InScopeRelation_SearchFieldLogListMap == null) {
            _id_InScopeRelation_SearchFieldLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_SearchFieldLogListMap.size() + 1);
        _id_InScopeRelation_SearchFieldLogListMap.put(key, subQuery);
        return "id_InScopeRelation_SearchFieldLogList." + key;
    }

    protected Map<String, ClickLogCQ> _id_NotInScopeRelation_ClickLogListMap;

    public Map<String, ClickLogCQ> getId_NotInScopeRelation_ClickLogList() {
        return _id_NotInScopeRelation_ClickLogListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_ClickLogList(
            final ClickLogCQ subQuery) {
        if (_id_NotInScopeRelation_ClickLogListMap == null) {
            _id_NotInScopeRelation_ClickLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_ClickLogListMap.size() + 1);
        _id_NotInScopeRelation_ClickLogListMap.put(key, subQuery);
        return "id_NotInScopeRelation_ClickLogList." + key;
    }

    protected Map<String, SearchFieldLogCQ> _id_NotInScopeRelation_SearchFieldLogListMap;

    public Map<String, SearchFieldLogCQ> getId_NotInScopeRelation_SearchFieldLogList() {
        return _id_NotInScopeRelation_SearchFieldLogListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_SearchFieldLogList(
            final SearchFieldLogCQ subQuery) {
        if (_id_NotInScopeRelation_SearchFieldLogListMap == null) {
            _id_NotInScopeRelation_SearchFieldLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_SearchFieldLogListMap.size() + 1);
        _id_NotInScopeRelation_SearchFieldLogListMap.put(key, subQuery);
        return "id_NotInScopeRelation_SearchFieldLogList." + key;
    }

    protected Map<String, ClickLogCQ> _id_QueryDerivedReferrer_ClickLogListMap;

    public Map<String, ClickLogCQ> getId_QueryDerivedReferrer_ClickLogList() {
        return _id_QueryDerivedReferrer_ClickLogListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_ClickLogList(
            final ClickLogCQ subQuery) {
        if (_id_QueryDerivedReferrer_ClickLogListMap == null) {
            _id_QueryDerivedReferrer_ClickLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_ClickLogListMap.size() + 1);
        _id_QueryDerivedReferrer_ClickLogListMap.put(key, subQuery);
        return "id_QueryDerivedReferrer_ClickLogList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_ClickLogListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_ClickLogListParameter() {
        return _id_QueryDerivedReferrer_ClickLogListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_ClickLogListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_ClickLogListParameterMap == null) {
            _id_QueryDerivedReferrer_ClickLogListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_ClickLogListParameterMap.size() + 1);
        _id_QueryDerivedReferrer_ClickLogListParameterMap.put(key,
                parameterValue);
        return "id_QueryDerivedReferrer_ClickLogListParameter." + key;
    }

    protected Map<String, SearchFieldLogCQ> _id_QueryDerivedReferrer_SearchFieldLogListMap;

    public Map<String, SearchFieldLogCQ> getId_QueryDerivedReferrer_SearchFieldLogList() {
        return _id_QueryDerivedReferrer_SearchFieldLogListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchFieldLogList(
            final SearchFieldLogCQ subQuery) {
        if (_id_QueryDerivedReferrer_SearchFieldLogListMap == null) {
            _id_QueryDerivedReferrer_SearchFieldLogListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_SearchFieldLogListMap.size() + 1);
        _id_QueryDerivedReferrer_SearchFieldLogListMap.put(key, subQuery);
        return "id_QueryDerivedReferrer_SearchFieldLogList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_SearchFieldLogListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_SearchFieldLogListParameter() {
        return _id_QueryDerivedReferrer_SearchFieldLogListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchFieldLogListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_SearchFieldLogListParameterMap == null) {
            _id_QueryDerivedReferrer_SearchFieldLogListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_SearchFieldLogListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_SearchFieldLogListParameterMap.put(key,
                parameterValue);
        return "id_QueryDerivedReferrer_SearchFieldLogListParameter." + key;
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
     * REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_RequestedTime_Asc() {
        regOBA("REQUESTED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)}
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
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsSearchLogCQ addOrderBy_UserSessionId_Asc() {
        regOBA("USER_SESSION_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * USER_SESSION_ID: {IX+, VARCHAR(100)}
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #FD4747">aliasName</span>);
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #FD4747">aliasName</span>);
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
    protected void reflectRelationOnUnionQuery(
            final ConditionQuery baseQueryAsSuper,
            final ConditionQuery unionQueryAsSuper) {
        final SearchLogCQ baseQuery = (SearchLogCQ) baseQueryAsSuper;
        final SearchLogCQ unionQuery = (SearchLogCQ) unionQueryAsSuper;
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
        final String nrp = resolveNextRelationPath("SEARCH_LOG", "userInfo");
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
    protected Map<String, SearchLogCQ> _scalarConditionMap;

    public Map<String, SearchLogCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final SearchLogCQ subQuery) {
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
    protected Map<String, SearchLogCQ> _specifyMyselfDerivedMap;

    public Map<String, SearchLogCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final SearchLogCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, SearchLogCQ> _queryMyselfDerivedMap;

    public Map<String, SearchLogCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final SearchLogCQ subQuery) {
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
    protected Map<String, SearchLogCQ> _myselfExistsMap;

    public Map<String, SearchLogCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final SearchLogCQ subQuery) {
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
    protected Map<String, SearchLogCQ> _myselfInScopeMap;

    public Map<String, SearchLogCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final SearchLogCQ subQuery) {
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
        return SearchLogCB.class.getName();
    }

    protected String xCQ() {
        return SearchLogCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
