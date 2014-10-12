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

import jp.sf.fess.db.cbean.SuggestBadWordCB;
import jp.sf.fess.db.cbean.cq.SuggestBadWordCQ;
import jp.sf.fess.db.cbean.cq.bs.AbstractBsSuggestBadWordCQ;
import jp.sf.fess.db.cbean.cq.bs.BsSuggestBadWordCQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of SUGGEST_BAD_WORD.
 * @author DBFlute(AutoGenerator)
 */
public class SuggestBadWordCIQ extends AbstractBsSuggestBadWordCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsSuggestBadWordCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SuggestBadWordCIQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsSuggestBadWordCQ myCQ) {
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
    protected ConditionValue getCValueSuggestWord() {
        return _myCQ.getSuggestWord();
    }

    @Override
    protected ConditionValue getCValueTargetRole() {
        return _myCQ.getTargetRole();
    }

    @Override
    protected ConditionValue getCValueTargetLabel() {
        return _myCQ.getTargetLabel();
    }

    @Override
    protected ConditionValue getCValueCreatedBy() {
        return _myCQ.getCreatedBy();
    }

    @Override
    protected ConditionValue getCValueCreatedTime() {
        return _myCQ.getCreatedTime();
    }

    @Override
    protected ConditionValue getCValueUpdatedBy() {
        return _myCQ.getUpdatedBy();
    }

    @Override
    protected ConditionValue getCValueUpdatedTime() {
        return _myCQ.getUpdatedTime();
    }

    @Override
    protected ConditionValue getCValueDeletedBy() {
        return _myCQ.getDeletedBy();
    }

    @Override
    protected ConditionValue getCValueDeletedTime() {
        return _myCQ.getDeletedTime();
    }

    @Override
    protected ConditionValue getCValueVersionNo() {
        return _myCQ.getVersionNo();
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String pp) {
        return null;
    }

    @Override
    public String keepScalarCondition(final SuggestBadWordCQ sq) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final SuggestBadWordCQ sq) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final SuggestBadWordCQ sq) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object vl) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final SuggestBadWordCQ sq) {
        throwIICBOE("MyselfExists");
        return null;
    }

    @Override
    public String keepMyselfInScope(final SuggestBadWordCQ sq) {
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
        return SuggestBadWordCB.class.getName();
    }

    protected String xinCQ() {
        return SuggestBadWordCQ.class.getName();
    }
}
