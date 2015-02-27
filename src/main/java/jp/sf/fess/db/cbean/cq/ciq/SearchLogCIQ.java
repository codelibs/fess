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

package jp.sf.fess.db.cbean.cq.ciq;

import java.util.Map;

import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.cbean.cq.ClickLogCQ;
import jp.sf.fess.db.cbean.cq.SearchFieldLogCQ;
import jp.sf.fess.db.cbean.cq.SearchLogCQ;
import jp.sf.fess.db.cbean.cq.UserInfoCQ;
import jp.sf.fess.db.cbean.cq.bs.AbstractBsSearchLogCQ;
import jp.sf.fess.db.cbean.cq.bs.BsSearchLogCQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of SEARCH_LOG.
 * @author DBFlute(AutoGenerator)
 */
public class SearchLogCIQ extends AbstractBsSearchLogCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsSearchLogCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SearchLogCIQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsSearchLogCQ myCQ) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
        _myCQ = myCQ;
        _foreignPropertyName = _myCQ.xgetForeignPropertyName(); // accept foreign property name
        _relationPath = _myCQ.xgetRelationPath(); // accept relation path
        _inline = true;
    }

    // ===================================================================================
    //                                                             Override about Register
    //                                                             =======================
    @Override
    protected void reflectRelationOnUnionQuery(final ConditionQuery bq,
            final ConditionQuery uq) {
        throw new IllegalConditionBeanOperationException(
                "InlineView cannot use Union: " + bq + " : " + uq);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(
            final ConditionKey k, final Object v, final ConditionValue cv,
            final String col) {
        regIQ(k, v, cv, col);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(
            final ConditionKey k, final Object v, final ConditionValue cv,
            final String col, final ConditionOption op) {
        regIQ(k, v, cv, col, op);
    }

    @Override
    protected void registerWhereClause(final String wc) {
        registerInlineWhereClause(wc);
    }

    @Override
    protected boolean isInScopeRelationSuppressLocalAliasName() {
        if (_onClause) {
            throw new IllegalConditionBeanOperationException(
                    "InScopeRelation on OnClause is unsupported.");
        }
        return true;
    }

    // ===================================================================================
    //                                                                Override about Query
    //                                                                ====================
    @Override
    protected ConditionValue getCValueId() {
        return _myCQ.getId();
    }

    @Override
    public String keepId_ExistsReferrer_ClickLogList(final ClickLogCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_ExistsReferrer_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_ClickLogList(final ClickLogCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_InScopeRelation_ClickLogList(final ClickLogCQ sq) {
        return _myCQ.keepId_InScopeRelation_ClickLogList(sq);
    }

    @Override
    public String keepId_InScopeRelation_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        return _myCQ.keepId_InScopeRelation_SearchFieldLogList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_ClickLogList(final ClickLogCQ sq) {
        return _myCQ.keepId_NotInScopeRelation_ClickLogList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        return _myCQ.keepId_NotInScopeRelation_SearchFieldLogList(sq);
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_ClickLogList(final ClickLogCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_ClickLogList(final ClickLogCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_ClickLogListParameter(
            final Object vl) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchFieldLogList(
            final SearchFieldLogCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_SearchFieldLogListParameter(
            final Object vl) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    protected ConditionValue getCValueSearchWord() {
        return _myCQ.getSearchWord();
    }

    @Override
    protected ConditionValue getCValueRequestedTime() {
        return _myCQ.getRequestedTime();
    }

    @Override
    protected ConditionValue getCValueResponseTime() {
        return _myCQ.getResponseTime();
    }

    @Override
    protected ConditionValue getCValueHitCount() {
        return _myCQ.getHitCount();
    }

    @Override
    protected ConditionValue getCValueQueryOffset() {
        return _myCQ.getQueryOffset();
    }

    @Override
    protected ConditionValue getCValueQueryPageSize() {
        return _myCQ.getQueryPageSize();
    }

    @Override
    protected ConditionValue getCValueUserAgent() {
        return _myCQ.getUserAgent();
    }

    @Override
    protected ConditionValue getCValueReferer() {
        return _myCQ.getReferer();
    }

    @Override
    protected ConditionValue getCValueClientIp() {
        return _myCQ.getClientIp();
    }

    @Override
    protected ConditionValue getCValueUserSessionId() {
        return _myCQ.getUserSessionId();
    }

    @Override
    protected ConditionValue getCValueAccessType() {
        return _myCQ.getAccessType();
    }

    @Override
    protected ConditionValue getCValueUserId() {
        return _myCQ.getUserId();
    }

    @Override
    public String keepUserId_InScopeRelation_UserInfo(final UserInfoCQ sq) {
        return _myCQ.keepUserId_InScopeRelation_UserInfo(sq);
    }

    @Override
    public String keepUserId_NotInScopeRelation_UserInfo(final UserInfoCQ sq) {
        return _myCQ.keepUserId_NotInScopeRelation_UserInfo(sq);
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String pp) {
        return null;
    }

    @Override
    public String keepScalarCondition(final SearchLogCQ sq) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final SearchLogCQ sq) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final SearchLogCQ sq) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object vl) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final SearchLogCQ sq) {
        throwIICBOE("MyselfExists");
        return null;
    }

    @Override
    public String keepMyselfInScope(final SearchLogCQ sq) {
        throwIICBOE("MyselfInScope");
        return null;
    }

    protected void throwIICBOE(final String name) {
        throw new IllegalConditionBeanOperationException(name
                + " at InlineView is unsupported.");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xinCB() {
        return SearchLogCB.class.getName();
    }

    protected String xinCQ() {
        return SearchLogCQ.class.getName();
    }
}
