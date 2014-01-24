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

package jp.sf.fess.db.cbean.cq.ciq;

import java.util.Map;

import jp.sf.fess.db.cbean.CrawlingSessionCB;
import jp.sf.fess.db.cbean.cq.CrawlingSessionCQ;
import jp.sf.fess.db.cbean.cq.CrawlingSessionInfoCQ;
import jp.sf.fess.db.cbean.cq.bs.AbstractBsCrawlingSessionCQ;
import jp.sf.fess.db.cbean.cq.bs.BsCrawlingSessionCQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of CRAWLING_SESSION.
 * @author DBFlute(AutoGenerator)
 */
public class CrawlingSessionCIQ extends AbstractBsCrawlingSessionCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsCrawlingSessionCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public CrawlingSessionCIQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsCrawlingSessionCQ myCQ) {
        super(childQuery, sqlClause, aliasName, nestLevel);
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
        final String msg = "InlineView must not need UNION method: " + bq
                + " : " + uq;
        throw new IllegalConditionBeanOperationException(msg);
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
    public String keepId_ExistsReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_InScopeRelation_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        return _myCQ.keepId_InScopeRelation_CrawlingSessionInfoList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        return _myCQ.keepId_NotInScopeRelation_CrawlingSessionInfoList(sq);
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_CrawlingSessionInfoListParameter(
            final Object pv) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    protected ConditionValue getCValueSessionId() {
        return _myCQ.getSessionId();
    }

    @Override
    protected ConditionValue getCValueName() {
        return _myCQ.getName();
    }

    @Override
    protected ConditionValue getCValueExpiredTime() {
        return _myCQ.getExpiredTime();
    }

    @Override
    protected ConditionValue getCValueCreatedTime() {
        return _myCQ.getCreatedTime();
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    @Override
    public String keepScalarCondition(final CrawlingSessionCQ subQuery) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final CrawlingSessionCQ subQuery) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final CrawlingSessionCQ subQuery) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object parameterValue) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final CrawlingSessionCQ subQuery) {
        throwIICBOE("MyselfExists");
        return null;
    }

    @Override
    public String keepMyselfInScope(final CrawlingSessionCQ subQuery) {
        throwIICBOE("MyselfInScope");
        return null;
    }

    protected void throwIICBOE(final String name) { // throwInlineIllegalConditionBeanOperationException()
        throw new IllegalConditionBeanOperationException(name
                + " at InlineView is unsupported.");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xinCB() {
        return CrawlingSessionCB.class.getName();
    }

    protected String xinCQ() {
        return CrawlingSessionCQ.class.getName();
    }
}
